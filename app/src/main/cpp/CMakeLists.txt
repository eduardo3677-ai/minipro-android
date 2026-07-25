cmake_minimum_required(VERSION 3.22.1)
project("minipro")

include(FetchContent)
FetchContent_Declare(
  minipro_c
  GIT_REPOSITORY https://gitlab.com/DavidGriffith/minipro.git
  GIT_TAG HEAD
)
FetchContent_MakeAvailable(minipro_c)

add_library(
        minipro
        SHARED
        minipro_jni.c
        ${minipro_c_SOURCE_DIR}/src/main.c
        ${minipro_c_SOURCE_DIR}/src/minipro.c
        ${minipro_c_SOURCE_DIR}/src/database.c
        ${minipro_c_SOURCE_DIR}/src/usb_nix.c
        ${minipro_c_SOURCE_DIR}/src/t48.c
        ${minipro_c_SOURCE_DIR}/src/tl866iiplus.c
        ${minipro_c_SOURCE_DIR}/src/bitbang.c
        ${minipro_c_SOURCE_DIR}/src/jedec.c
)

target_include_directories(minipro PRIVATE ${minipro_c_SOURCE_DIR}/src)

find_library(log-lib log)

target_link_libraries(
        minipro
        ${log-lib}
)
