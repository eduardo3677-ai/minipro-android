package com.echosmart.flashlabs.hardware;

/**
 * Matriz completa de comandos y opcodes oficiales del protocolo XGecu T48.
 */
public class T48ProtocolCommands {
    // VIDs & PIDs del T48
    public static final int VID_MICROCHIP = 0x04D8;
    public static final int PID_TL866 = 0xE11C;
    public static final int VID_XGECU = 0xA466;
    public static final int PID_T48_TYPE_A = 0x0A53;
    public static final int PID_T48_TYPE_B = 0x1A86;

    // Opcodes principales
    public static final byte OP_NAND_INIT = 0x02;
    public static final byte OP_BEGIN_TRANS = 0x03;
    public static final byte OP_END_TRANS = 0x04;
    public static final byte OP_READID = 0x05;
    public static final byte OP_READ_USER = 0x06;
    public static final byte OP_WRITE_USER = 0x07;
    public static final byte OP_READ_CFG = 0x08;
    public static final byte OP_WRITE_CFG = 0x09;
    public static final byte OP_WRITE_USER_DATA = 0x0A;
    public static final byte OP_READ_USER_DATA = 0x0B;
    public static final byte OP_WRITE_CODE = 0x0C;
    public static final byte OP_READ_CODE = 0x0D;
    public static final byte OP_ERASE = 0x0E;
    public static final byte OP_TEST_RAM = 0x0F;
    public static final byte OP_READ_DATA = 0x10;
    public static final byte OP_WRITE_DATA = 0x11;
    public static final byte OP_WRITE_LOCK = 0x14;
    public static final byte OP_READ_LOCK = 0x15;
    public static final byte OP_READ_CALIBRATION = 0x16;
    public static final byte OP_PROTECT_OFF = 0x18;
    public static final byte OP_PROTECT_ON = 0x19;
    public static final byte OP_READ_JEDEC = 0x1D;
    public static final byte OP_WRITE_JEDEC = 0x1E;
    public static final byte OP_LOGIC_IC_TEST = 0x28;
    public static final byte OP_AUTODETECT = 0x37;
    public static final byte OP_UNLOCK_TSOP48 = 0x38;
    public static final byte OP_REQUEST_STATUS = 0x39;

    // Control de Hardware Bit-Banging & Voltajes
    public static final byte OP_SET_VCC_VOLTAGE = 0x1B;
    public static final byte OP_SET_VPP_VOLTAGE = 0x1C;
    public static final byte OP_RESET_PIN_DRIVERS = 0x2D;
    public static final byte OP_SET_VCC_PIN = 0x2E;
    public static final byte OP_SET_VPP_PIN = 0x2F;
    public static final byte OP_SET_GND_PIN = 0x30;
    public static final byte OP_SET_PULLUPS = 0x31;
    public static final byte OP_SET_PULLDOWNS = 0x32;
    public static final byte OP_MEASURE_VOLTAGES = 0x33;
    public static final byte OP_READ_PINS = 0x35;
    public static final byte OP_SET_OUT = 0x36;

    // Bootloader y Firmware
    public static final byte OP_BOOTLOADER_WRITE = 0x3B;
    public static final byte OP_BOOTLOADER_ERASE = 0x3C;
    public static final byte OP_RESET = 0x3F;
}
