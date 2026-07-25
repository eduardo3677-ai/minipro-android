package com.echosmart.flashlabs.data.model

data class ChipInfo(
    val name: String,
    val manufacturer: String,
    val category: String,
    val packageType: String,
    val sizeBytes: Int,
    val vccVoltage: Float,
    val vppVoltage: Float
) {
    override fun toString(): String {
        return "$name [$manufacturer] - $category ($packageType)"
    }
}
