package com.example.androidinstrumentation

import android.app.Application
import com.jio.otel.JioOTelConfig
import com.jio.otel.JioOTelManager
import com.jio.otel.LogProfile
class OTelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeOpenTelemetry()
    }
    private fun initializeOpenTelemetry() {
        // Each client app supplies its own values here — endpoint, headers, auth, identity.
        val config = JioOTelConfig.Builder()
            .serviceName("jio-hcmp-deeptrace-android-new")
            .assetId("427891281674")
            .otlpEndpoint(BuildConfig.OTLP_ENDPOINT)
            .apiKey("REPLACE_WITH_CLIENT_TOKEN")
            .exportHeader("X-Tenant-Id", "demo-tenant")
            .addAttribute("deployment.environment", "dev")
            .addAttribute("routing_android", "1")
            .enableNetworkMonitoring(true)
            .enableActivityInstrumentation(true)
            .otlpHttpCompressionGzip(enabled = true)
            .logProfile(LogProfile.FATALS_ONLY)
            .debugLogging(true)
            .scheduledDelayMillis(2_000)        // normal logs ship within ~2s
            .maxQueueSize(2_048)                // keep default, plenty of headroom
            .maxExportBatchSize(512)            // keep default
            .exporterTimeoutMillis(2_000)       // tight cap on flush during shutdown (see note)
            .build()
        JioOTelManager.initialize(this, config)
    }
}
