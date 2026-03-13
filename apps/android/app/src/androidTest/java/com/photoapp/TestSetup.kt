package com.photoapp

import androidx.test.platform.app.InstrumentationRegistry
import java.io.FileInputStream

fun resetAppState() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val packageName = instrumentation.targetContext.packageName

    val commandOutput = instrumentation.uiAutomation
        .executeShellCommand("pm clear $packageName")
        .use { descriptor ->
            FileInputStream(descriptor.fileDescriptor).bufferedReader().use { it.readText() }
        }

    check(commandOutput.contains("Success")) {
        "failed to clear app state: $commandOutput"
    }
}
