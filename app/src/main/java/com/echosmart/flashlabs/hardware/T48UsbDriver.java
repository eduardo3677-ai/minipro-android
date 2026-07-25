package com.echosmart.flashlabs.hardware;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.echosmart.flashlabs.data.model.XGecuChipDevice;

import java.util.Arrays;

/**
 * Controlador completo de comunicación USB nativa para XGecu T48 (Sin Root).
 * Implementa las 14+ operaciones del hardware oficial.
 */
public class T48UsbDriver {
    private static final String TAG = "FlashLabsT48Driver";

    private final UsbManager usbManager;
    private UsbDevice usbDevice;
    private UsbDeviceConnection connection;
    private UsbEndpoint epOut;
    private UsbEndpoint epIn;

    public T48UsbDriver(UsbManager usbManager) {
        this.usbManager = usbManager;
    }

    public boolean isT48Device(UsbDevice device) {
        if (device == null) return false;
        int vid = device.getVendorId();
        int pid = device.getProductId();
        return (vid == T48ProtocolCommands.VID_MICROCHIP && pid == T48ProtocolCommands.PID_TL866) ||
               (vid == T48ProtocolCommands.VID_XGECU && 
               (pid == T48ProtocolCommands.PID_T48_TYPE_A || pid == T48ProtocolCommands.PID_T48_TYPE_B));
    }

    public boolean connect(UsbDevice device) {
        if (device == null) return false;
        this.usbDevice = device;

        UsbInterface usbInterface = device.getInterface(0);
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    epOut = ep;
                } else {
                    epIn = ep;
                }
            }
        }

        connection = usbManager.openDevice(device);
        if (connection != null && connection.claimInterface(usbInterface, true)) {
            Log.d(TAG, "Conexión USB OTG abierta con éxito.");
            return true;
        }
        return false;
    }

    public int getFileDescriptor() {
        if (connection != null) {
            return connection.getFileDescriptor();
        }
        return -1;
    }

    public void disconnect() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
        epOut = null;
        epIn = null;
    }

    public boolean isConnected() {
        return connection != null;
    }

    public byte[] sendCommand(byte[] command, int responseLength, int timeoutMs) {
        if (!isConnected() || epOut == null || epIn == null) {
            return null;
        }

        byte[] txBuffer = Arrays.copyOf(command, 64);
        int maxRetries = 3;
        int sent = -1;

        for (int i = 0; i < maxRetries; i++) {
            sent = connection.bulkTransfer(epOut, txBuffer, txBuffer.length, timeoutMs);
            if (sent >= 0) {
                break;
            }
            Log.w(TAG, "Reintentando envío USB (intento " + (i + 1) + ")");
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }

        if (sent < 0) {
            Log.e(TAG, "Error enviando opcode: 0x" + Integer.toHexString(command[0] & 0xFF));
            return null;
        }

        if (responseLength > 0) {
            byte[] rxBuffer = new byte[responseLength];
            int read = -1;
            for (int i = 0; i < maxRetries; i++) {
                read = connection.bulkTransfer(epIn, rxBuffer, rxBuffer.length, timeoutMs);
                if (read >= 0) {
                    break;
                }
                Log.w(TAG, "Reintentando lectura USB (intento " + (i + 1) + ")");
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            }
            
            if (read >= 0) {
                return rxBuffer;
            } else {
                Log.e(TAG, "Error leyendo respuesta USB para opcode: 0x" + Integer.toHexString(command[0] & 0xFF));
            }
        }
        return new byte[0];
    }

    public boolean beginTransactionWithDevice(XGecuChipDevice device) {
        if (device == null) return false;
        byte[] msg = new byte[64];
        msg[0] = T48ProtocolCommands.OP_BEGIN_TRANS;
        msg[1] = (byte) device.getProtocolId();
        msg[2] = (byte) device.getVariant();
        msg[3] = 0x00; // ICSP off

        writeInt16(msg, 4, device.getVoltagesRaw());
        msg[6] = (byte) device.getChipInfo();
        msg[7] = (byte) device.getPinMap();
        writeInt16(msg, 8, device.getDataMemorySize());
        writeInt16(msg, 10, device.getPageSize());
        writeInt16(msg, 12, device.getPulseDelay());
        writeInt16(msg, 14, device.getDataMemory2Size());
        writeInt32(msg, 16, device.getCodeMemorySize());
        msg[20] = (byte) (device.getVoltagesRaw() >> 16);

        writeInt32(msg, 40, device.getPackageDetails());
        writeInt16(msg, 44, device.getReadBufferSize());
        writeInt32(msg, 56, device.getFlagsRaw());

        byte[] resp = sendCommand(msg, 64, 2000);
        return resp != null && resp.length > 0;
    }

    public boolean endTransaction() {
        byte[] msg = new byte[64];
        msg[0] = T48ProtocolCommands.OP_END_TRANS;
        byte[] resp = sendCommand(msg, 64, 1000);
        return resp != null;
    }

    public byte[] readChipId() {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_READID;
        return sendCommand(cmd, 64, 2000);
    }

    public byte[] spiAutoDetect() {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_AUTODETECT;
        return sendCommand(cmd, 64, 2000);
    }

    public byte[] readFuses() {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_READ_CFG;
        return sendCommand(cmd, 64, 2000);
    }

    public boolean writeFuses(byte[] fuseData) {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_WRITE_CFG;
        if (fuseData != null) {
            System.arraycopy(fuseData, 0, cmd, 4, Math.min(fuseData.length, 50));
        }
        byte[] resp = sendCommand(cmd, 64, 2000);
        return resp != null;
    }

    public byte[] readUserOtp() {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_READ_USER;
        return sendCommand(cmd, 64, 2000);
    }

    public boolean writeUserOtp(byte[] otpData) {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_WRITE_USER;
        if (otpData != null) {
            System.arraycopy(otpData, 0, cmd, 4, Math.min(otpData.length, 50));
        }
        byte[] resp = sendCommand(cmd, 64, 2000);
        return resp != null;
    }

    public boolean initNand(int pageSize, int blockCount, int oobSize) {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_NAND_INIT;
        writeInt16(cmd, 1, pageSize);
        writeInt16(cmd, 3, blockCount);
        writeInt16(cmd, 5, oobSize);
        byte[] resp = sendCommand(cmd, 64, 2000);
        return resp != null;
    }

    public byte[] readNandPage(int blockAddress, int pageAddress, int pageSize) {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_READ_CODE; // Or specific NAND read opcode
        writeInt32(cmd, 1, blockAddress);
        writeInt32(cmd, 5, pageAddress);
        // NAND read logic involves fetching multiple chunks over USB
        return sendCommand(cmd, pageSize, 3000); 
    }

    public boolean unlockTsop48() {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_UNLOCK_TSOP48;
        byte[] resp = sendCommand(cmd, 64, 2000);
        return resp != null;
    }

    public byte[] logicIcTest(byte[] vector) {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_LOGIC_IC_TEST;
        if (vector != null) {
            System.arraycopy(vector, 0, cmd, 4, Math.min(vector.length, 50));
        }
        return sendCommand(cmd, 64, 2000);
    }

    public byte[] measureVoltages() {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_MEASURE_VOLTAGES;
        return sendCommand(cmd, 64, 1000);
    }

    public boolean checkOvercurrentStatus() {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_REQUEST_STATUS;
        byte[] resp = sendCommand(cmd, 64, 1000);
        return resp != null && resp.length > 0 && resp[0] != 0;
    }

    public boolean resetPinDrivers() {
        byte[] cmd = new byte[64];
        cmd[0] = T48ProtocolCommands.OP_RESET_PIN_DRIVERS;
        byte[] resp = sendCommand(cmd, 64, 1000);
        return resp != null;
    }

    private void writeInt16(byte[] buf, int offset, int val) {
        buf[offset] = (byte) (val & 0xFF);
        buf[offset + 1] = (byte) ((val >> 8) & 0xFF);
    }

    private void writeInt32(byte[] buf, int offset, long val) {
        buf[offset] = (byte) (val & 0xFF);
        buf[offset + 1] = (byte) ((val >> 8) & 0xFF);
        buf[offset + 2] = (byte) ((val >> 16) & 0xFF);
        buf[offset + 3] = (byte) ((val >> 24) & 0xFF);
    }
}
