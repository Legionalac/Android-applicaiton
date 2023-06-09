#include "nikola_pavicevic_shoppinglist_JNI.h"

JNIEXPORT jint JNICALL Java_nikola_pavicevic_shoppinglist_JNI_increment(JNIEnv *env, jobject jobj, jint x){
    return ++x;
}