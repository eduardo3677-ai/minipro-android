cmake_minimum_required(VERSION 3.22.1)
project("minipro")

add_library(
        minipro
        SHARED
        minipro_jni.c
        minipro_src/main.c
        minipro_src/minipro.c
        minipro_src/database.c
        minipro_src/usb_nix.c
        minipro_src/t48.c
        minipro_src/tl866iiplus.c
        minipro_src/bitbang.c
        minipro_src/jedec.c
)

target_include_directories(minipro PRIVATE minipro_src)

find_library(log-lib log)

target_link_libraries(
        minipro
        ${log-lib}
)
