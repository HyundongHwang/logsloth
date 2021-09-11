################################################################################
# clean build, run
# rm ./CMakeFiles/ -rf; rm ./CMakeCache.txt; cmake .; make;



################################################################################
# env setting
CMAKE_MINIMUM_REQUIRED(VERSION 2.6)
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
set(CMAKE_BUILD_TYPE Debug)


################################################################################
# gtest
INCLUDE_DIRECTORIES( ../thirdparty/gtest/include )
ADD_LIBRARY( gtest STATIC IMPORTED )
SET_TARGET_PROPERTIES( gtest PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../thirdparty/gtest/bin/Linux/libgtest.a )
MESSAGE("GOOGLETEST LIB PATH : ${CMAKE_SOURCE_DIR}/thirdparty/gtest/bin/Linux/libgtest.a")



################################################################################
# main build
ADD_EXECUTABLE( test_app
                stdafx.cpp
                main.cpp
                test.cpp)

TARGET_LINK_LIBRARIES(  test_app
        gtest
        pthread)

ADD_EXECUTABLE( test_enter_first_app
        stdafx.cpp
        main.cpp
        test_enter_first.cpp)

TARGET_LINK_LIBRARIES(  test_enter_first_app
        gtest
        pthread)

ADD_EXECUTABLE( test_callstack_first_app
        stdafx.cpp
        main.cpp
        test_callstack_first.cpp)

TARGET_LINK_LIBRARIES(  test_callstack_first_app
        gtest
        pthread)