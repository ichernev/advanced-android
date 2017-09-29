cmake_minimum_required(VERSION 3.4.1)
# set( CMAKE_CXX_STANDARD 11 )

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Werror")

add_library(hello-jni SHARED
            hello-jni.cpp)

# Include libraries needed for hello-jni lib
target_link_libraries(hello-jni
                      android
                      log)
