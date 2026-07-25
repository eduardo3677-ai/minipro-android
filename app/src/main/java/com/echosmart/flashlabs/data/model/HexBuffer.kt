package com.echosmart.flashlabs.data.model

data class HexBuffer(
    val data: ByteArray = ByteArray(256) { 0xFF.toByte() },
    val size: Int = data.size
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HexBuffer
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }
}
