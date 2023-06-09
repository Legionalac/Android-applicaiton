LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := IncrementLibrary
LOCAL_SRC_FILES := nikola_pavicevic_shoppinglist_JNI.cpp
include $(BUILD_SHARED_LIBRARY)
