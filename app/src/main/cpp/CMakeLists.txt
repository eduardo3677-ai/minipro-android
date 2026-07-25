cmake_minimum_required(VERSION 3.22.1)
project("minipro")

# Compile libusb from local source
add_library(libusb_local STATIC
    libusb/libusb/core.c
    libusb/libusb/descriptor.c
    libusb/libusb/hotplug.c
    libusb/libusb/io.c
    libusb/libusb/sync.c
    libusb/libusb/strerror.c
    libusb/libusb/os/linux_usbfs.c
    libusb/libusb/os/linux_netlink.c
    libusb/libusb/os/threads_posix.c
)
target_include_directories(libusb_local PUBLIC libusb/libusb libusb/android)
target_compile_definitions(libusb_local PRIVATE OS_LINUX PLATFORM_POSIX HAVE_LINUX_NETLINK_H)

add_library(
        minipro
        SHARED
        minipro_jni.c
        minipro_src/main.c
        minipro_src/minipro.c
        minipro_src/database.c
        minipro_src/usb_nix.c
        minipro_src/t48.c
        minipro_src/t56.c
        minipro_src/t76.c
        minipro_src/tl866iiplus.c
        minipro_src/tl866a.c
        minipro_src/bitbang.c
        minipro_src/jedec.c
        minipro_src/xml.c
        minipro_src/ihex.c
        minipro_src/srec.c
        minipro_src/prom.c
        minipro_src/cdecode.c
        minipro_src/cencode.c
        minipro_src/version.c
)

target_include_directories(minipro PRIVATE minipro_src libusb/libusb)
target_compile_definitions(minipro PRIVATE SHARE_INSTDIR="")

find_library(log-lib log)

target_link_libraries(
        minipro
        libusb_local
        ${log-lib}
)
