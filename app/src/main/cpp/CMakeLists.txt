cmake_minimum_required(VERSION 3.22.1)
project("minipro")

add_library(
        minipro
        SHARED
        minipro_jni.c
        ../../../../../src/main.c
        ../../../../../src/minipro.c
        ../../../../../src/database.c
        ../../../../../src/usb_nix.c
        ../../../../../src/t48.c
        ../../../../../src/tl866iiplus.c
        ../../../../../src/bitbang.c
        ../../../../../src/jedec.c
)

target_include_directories(minipro PRIVATE ../../../../../src)

find_library(log-lib log)

target_link_libraries(
        minipro
        ${log-lib}
)
