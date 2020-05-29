package nl.spijkerman.wangaponzi.model

data class Rate(val history: List<Tick>) {
    enum class Type(private val min: Long,
                    private val max: Long) {
        GREEN(14800000, 24410000),
        RED(3780000, 103089996);

        // https://en.wikipedia.org/wiki/Feature_scaling
        fun scale(lo: Double,
                  hi: Double,
                  input: Double): Double =
                lo + (input - min) * (hi - lo) / (max - min)
    }

    val current = history.last().value

    @Suppress("unused")
    val min = history.map { it.value }.min()

    @Suppress("unused")
    val max = history.map { it.value }.max()
}