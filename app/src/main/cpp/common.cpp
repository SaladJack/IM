//
// Created by SaladJack on 2017/3/2.
//

#include "common.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/system_properties.h>


/**
 * Get the version of current SDK.
 */
int get_version() {
    char value[8] = "";
    __system_property_get("ro.build.version.sdk", value);
    return atoi(value);
}

/**
 * stitch(combine) two string to one string
 *
 * @param  str1 the first string to be stitched
 * @param  str2 the second string to be stitched
 * @return      stitched string
 */
char *str_stitching(const char *str1, const char *str2) {
    char *result;
    result = (char *) malloc(strlen(str1) + strlen(str2) + 1);
    if (!result) {
        return NULL;
    }

    strcpy(result, str1);//result = str1;
    strcat(result, str2);//result = result + str2;

    return result;
}