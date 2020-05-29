package nl.spijkerman.wangaponzi.service

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import nl.spijkerman.wangaponzi.model.Rate
import nl.spijkerman.wangaponzi.model.Rate.Type.GREEN
import nl.spijkerman.wangaponzi.model.Rate.Type.RED
import nl.spijkerman.wangaponzi.model.Transaction
import org.springframework.stereotype.Service
import java.io.*
import java.lang.System.currentTimeMillis
import java.time.LocalTime
import kotlin.streams.toList


@Service
class TransactionService(private val rateService: RateService) {

    private var lastModified: Long = Long.MIN_VALUE
    private lateinit var cache: List<Transaction>

    private companion object {
        private const val TIMEOUT = 3000L
        private const val SHEET_ID = "1ZL9JJc85_aDmcJZVRFnQjCwYxwlGAmlc1zkPcsu01eE"
        private const val APPLICATION_NAME = "Google Sheets API Java Quickstart"
        private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
        private const val TOKENS_DIRECTORY_PATH = "tokens"

        /**
         * Global instance of the scopes required by this quickstart.
         * If modifying these scopes, delete your previously saved tokens/ folder.
         */
        private val SCOPES = listOf(SheetsScopes.SPREADSHEETS_READONLY)
        private const val CREDENTIALS_FILE_PATH = "/credentials.json"
    }


    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential? {
        // Load client secrets.
        val `in`: InputStream = javaClass.getResourceAsStream(CREDENTIALS_FILE_PATH)
                ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }


    fun getAll(): List<Transaction> {
        if (lastModified + TIMEOUT < currentTimeMillis()) {
            // Build a new authorized API client service.
            val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
            val spreadsheetId = SHEET_ID
            val range = "transactions!A2:D"
            val service: Sheets = Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                    .setApplicationName(APPLICATION_NAME)
                    .build()
            val response: ValueRange = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute()
            val values: List<List<Any>> = response.getValues()

            cache = values.parallelStream().map {
                val moment = LocalTime.parse(it[0].toString().split(" ")[1])
                val user = it[1].toString()
                val amount = it[3].toString().toInt()

                var money = 0.0
                var green = 0L
                var red = 0L

                fun profit(type: Rate.Type): Double = amount * rateService.get(type, moment).current

                when (it[2].toString()) {
                    "Ontvangen Wanguldens" ->
                        money += amount
                    "Kopen Rood" -> {
                        red += amount
                        money -= profit(RED)
                    }
                    "Kopen Groen" -> {
                        green += amount
                        money -= profit(GREEN)
                    }
                    "Verkopen Rood" -> {
                        red -= amount
                        money += profit(RED)
                    }
                    "Verkopen Groen" -> {
                        green -= amount
                        money += profit(GREEN)
                    }
                    else -> throw RuntimeException("Unexpected action: ${it[2]}")
                }

                Transaction(moment, user, money, red, green)
            }.toList()
            lastModified = currentTimeMillis()
            println("Refreshed transaction cache")
        }
        return cache
    }
}