package com.waltsrally.rallyhublive.data.models

import com.google.gson.annotations.SerializedName

// ── Auth ──────────────────────────────────────────────────────────────────────

data class LoginRequest(
    val username: String,
    val password: String,
    val type: String           // "marshal" | "driver"
)

data class UserInfo(
    val id: Int? = null,
    val name: String,
    val role: String,          // "marshal" | "driver" | "admin" | "tc"
    val type: String? = null,  // "marshal" | "driver"
    val car: String? = null,   // driver's car number
    val cp: String? = null,    // marshal checkpoint type
    val co: String? = null     // co-driver name
)

data class LoginResponse(
    val ok: Boolean? = null,
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
    val ok: Boolean? = null,
    val sections: List<RhlSection>? = null,
    @SerializedName("event_id") val eventId: Int? = null
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
    val ok: Boolean? = null,
    val cars: List<CarEntry>? = null,
    @SerializedName("section_id") val sectionId: Int? = null
)

// ── Start ─────────────────────────────────────────────────────────────────────

data class RecordStartRequest(
    @SerializedName("section_id") val sectionId: Int,
    @SerializedName("car_number") val carNumber: String
)

data class RecordStartResponse(
    val ok: Boolean? = null,
    val car: String? = null,
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
    val ok: Boolean? = null,
    val car: String? = null,
    val error: String? = null
)

// ── DNF ───────────────────────────────────────────────────────────────────────

data class RecordDNFRequest(
    @SerializedName("section_id") val sectionId: Int,
    @SerializedName("car_number") val carNumber: String
)

data class RecordDNFResponse(
    val ok: Boolean? = null,
    val car: String? = null,
    val error: String? = null
)

// ── Results ───────────────────────────────────────────────────────────────────

data class StageResult(
    @SerializedName("car_number") val carNumber: String? = null,
    @SerializedName("driver_name") val driverName: String? = null,
    @SerializedName("section_code") val sectionCode: String? = null,
    @SerializedName("section_name") val sectionName: String? = null,
    @SerializedName("start_time") val startTime: String? = null,
    @SerializedName("finish_time") val finishTime: String? = null,
    // server sends "elapsed_seconds" (not ms) as a string like "27.395"
    @SerializedName("elapsed_seconds") val elapsedSeconds: String? = null,
    @SerializedName("elapsed_ms") val elapsedMs: Double? = null,
    @SerializedName("ff_time") val ffTime: String? = null,
    @SerializedName("is_dnf") val isDnf: Int? = null,
    @SerializedName("marshal_name") val marshalName: String? = null,
    val status: String? = null
)

// recent_times returns {times:[...]}, driver_results returns {results:[...]}
data class ResultsResponse(
    val ok: Boolean? = null,
    val results: List<StageResult>? = null,
    val times: List<StageResult>? = null,   // used by recent_times endpoint
    val car: String? = null
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
    val ok: Boolean? = null,
    val board: List<BoardEntry>? = null
)

// ── Admin set start time ──────────────────────────────────────────────────────

data class SetAdminStartRequest(
    @SerializedName("section_id") val sectionId: Int,
    @SerializedName("car_number") val carNumber: String,
    @SerializedName("start_time") val startTime: String
)

// ── Generic OK ────────────────────────────────────────────────────────────────

data class OkResponse(
    val ok: Boolean? = null,
    val error: String? = null
)
