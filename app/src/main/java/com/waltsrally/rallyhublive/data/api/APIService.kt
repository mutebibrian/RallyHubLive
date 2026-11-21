package com.waltsrally.rallyhublive.data.api

import com.waltsrally.rallyhublive.data.models.*
import retrofit2.http.*

interface APIService {

    // ── Auth ──────────────────────────────────────────────────────────────────
    @POST("rally_hub_live.php")
    @Headers("Content-Type: application/json")
    suspend fun login(
        @Query("api") api: String = "login",
        @Body body: LoginRequest
    ): LoginResponse

    // ── Sections ──────────────────────────────────────────────────────────────
    @GET("rally_hub_live.php")
    suspend fun getSections(
        @Query("api") api: String = "get_sections",
        @Query("token") token: String,
        @Query("event_id") eventId: Int = 13
    ): SectionsResponse

    // ── Cars ──────────────────────────────────────────────────────────────────
    @GET("rally_hub_live.php")
    suspend fun getAllCars(
        @Query("api") api: String = "get_all_cars",
        @Query("token") token: String,
        @Query("section_id") sectionId: Int,
        @Query("event_id") eventId: Int = 13
    ): CarsResponse

    // ── Start marshal ─────────────────────────────────────────────────────────
    @POST("rally_hub_live.php")
    @Headers("Content-Type: application/json")
    suspend fun recordStart(
        @Query("api") api: String = "record_start",
        @Query("token") token: String,
        @Body body: RecordStartRequest
    ): RecordStartResponse

    // ── Admin: set start time ──────────────────────────────────────────────────
    @POST("rally_hub_live.php")
    @Headers("Content-Type: application/json")
    suspend fun setAdminStartTime(
        @Query("api") api: String = "set_admin_start_time",
        @Query("token") token: String,
        @Body body: SetAdminStartRequest
    ): RecordStartResponse

    // ── Time entry (finish) ───────────────────────────────────────────────────
    @POST("rally_hub_live.php")
    @Headers("Content-Type: application/json")
    suspend fun recordTime(
        @Query("api") api: String = "record_time",
        @Query("token") token: String,
        @Body body: RecordTimeRequest
    ): RecordTimeResponse

    // ── DNF ───────────────────────────────────────────────────────────────────
    @POST("rally_hub_live.php")
    @Headers("Content-Type: application/json")
    suspend fun recordDnf(
        @Query("api") api: String = "record_dnf",
        @Query("token") token: String,
        @Body body: RecordDNFRequest
    ): RecordDNFResponse

    // ── Results ───────────────────────────────────────────────────────────────
    @GET("rally_hub_live.php")
    suspend fun getResults(
        @Query("api") api: String = "get_results",
        @Query("token") token: String,
        @Query("section_id") sectionId: Int,
        @Query("event_id") eventId: Int = 13
    ): ResultsResponse

    @GET("rally_hub_live.php")
    suspend fun getBoard(
        @Query("api") api: String = "get_board",
        @Query("token") token: String,
        @Query("section_id") sectionId: Int,
        @Query("event_id") eventId: Int = 13
    ): BoardResponse

    @GET("rally_hub_live.php")
    suspend fun driverResults(
        @Query("api") api: String = "driver_results",
        @Query("token") token: String,
        @Query("event_id") eventId: Int = 13
    ): ResultsResponse
}
