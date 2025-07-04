package no.fritjof.dashboard.model

data class Athlete(
    val fullName: String,
    var movingTime: Double = 0.0,
    var elevationGain: Double = 0.0,
    var totalDistance: Double = 0.0,
    var numberOfActivities: Int = 0,
    var longestActivity: Double = 0.0,
    var averagePacePrKm: String = "N/A",
) {
    fun addActivity(movingTime: Double, elevationGain: Double, distance: Double) {
        this.movingTime += movingTime
        this.elevationGain += elevationGain
        this.totalDistance += distance
        this.numberOfActivities += 1
        this.longestActivity = maxOf(this.longestActivity, distance)
    }

    fun setAveragePacePerKm() {
        if (totalDistance != 0.0) {
            val paceSeconds = movingTime / (totalDistance / 1000) // seconds per km
            val minutes = (paceSeconds / 60).toInt()
            val seconds = (paceSeconds % 60).toInt()

            this.averagePacePrKm = String.format("%d:%02d /km", minutes, seconds)
        }
    }

}