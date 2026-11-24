package com.waltsrally.rallyhublive.data.models

import com.google.gson.annotations.SerializedName

// ── Auth ──────────────────────────────────────────────────────────────────────

data class LoginRequest(
    val username: String,
    val password: String,
    val type: String           // "marshal" | "driver"
)

data class UserInfo(
    val id: Int,
    val name: String,
    val role: String,          // "marshal" | "driver" | "admin" | "tc"
    @SerializedName("car_number") val carNumber: String? = null,
    val token: String
)

data class LoginResponse(
    val ok: Boolean,
    val user: UserInfo? = null,
    val error: String? = null
)

// ── Sections ──────────────────────────────────────────────────────────────────

data class RhlSection(
    val id: Int,
    @SerializedName("section_code") val sectionCode: String,
    @SerializedName("section_name") val sectionName: String,
    @SerializedName("distance_km") val distanceKm: Double,
    val leg: Int,
    val status: String = "pending"   // "pending" | "running" | "complete"
)

data class SectionsResponse(
    val ok: Boolean,
    val sections: List<RhlSection> = emptyList()
)

// ── Cars ──────────────────────────────────────────────────────────────────────

data class CarEntry(
    @SerializedName("car_number") val carNumber: String,
    @SerializedName("driver_name") val driverName: String,
    @SerializedName("co_driver_name") val coDriverName: String = "",
    @SerializedName("car_class") val carClass: String = "",
    @SerializedName("start_time") val startTime: String? = null,  // null = not yet started
    @SerializedName("ff_id") val ffId: String? = null             // null = not yet finished
) {
    val hasFinished: Boolean get() = ffId != null
}

data class CarsResponse(
    val ok: Boolean,
    val cars: List<CarEntry> = emptyList()
)

// ── Start ─────────────────────────────────────────────────────────────────────

data class RecordStartRequest(
    @SerializedName("section_id") val sectionId: Int,
    @SerializedName("car_number") val carNumber: String
)

data class RecordStartResponse(
    val ok: Boolean,
    val car: String,
    @SerializedName("start_time") val startTime: String? = null,
    val error: String? = null
)

// ── Time entry / TC ───────────────────────────────────────────────────────────

data class RecordTimeRequest(
    @SerializedName("section_id") val sectionId: Int,
    @SerializedName("car_number") val carNumber: String,
    @SerializedName("finish_time") val finishTime: String? = null
)

data class RecordTimeResponse(
    val ok: Boolean,
    val car: String? = null,
    val error: String? = null
)

// ── DNF ───────────────────────────────────────────────────────────────────────

data class RecordDNFRequest(
    @SerializedName("section_id") val sectionId: Int,
    @SerializedName("car_number") val carNumber: String
)

data class RecordDNFResponse(
    val ok: Boolean,
    val car: String? = null,
    val error: String? = null
)

// ── Results ───────────────────────────────────────────────────────────────────

data class StageResult(
    @SerializedName("car_number") val carNumber: String,
    @SerializedName("driver_name") val driverName: String = "",
    @SerializedName("section_code") val sectionCode: String = "",
    @SerializedName("section_name") val sectionName: String = "",
    @SerializedName("start_time") val startTime: String? = null,
    @SerializedName("finish_time") val finishTime: String? = null,
    @SerializedName("elapsed_ms") val elapsedMs: Double? = null,
    val status: String = ""
)

data class ResultsResponse(
    val ok: Boolean,
    val results: List<StageResult> = emptyList()
)

// ── Board ─────────────────────────────────────────────────────────────────────

data class BoardEntry(
    @SerializedName("car_number") val carNumber: String,
    @SerializedName("driver_name") val driverName: String = "",
    @SerializedName("co_driver_name") val coDriverName: String = "",
    @SerializedName("car_class") val carClass: String = "",
    @SerializedName("start_time") val startTime: String? = null,
    @SerializedName("finish_time") val finishTime: String? = null,
    val elapsed: String? = null,
    val position: Int? = null
)

data class BoardResponse(
    val ok: Boolean,
    val board: List<BoardEntry> = emptyList()
)

// ── Admin set start time ──────────────────────────────────────────────────────

data class SetAdminStartRequest(
    @SerializedName("section_id") val sectionId: Int,
    @SerializedName("car_number") val carNumber: String,
    @SerializedName("start_time") val startTime: String
)

// ── Generic OK ────────────────────────────────────────────────────────────────

data class OkResponse(
    val ok: Boolean,
    val error: String? = null
)
