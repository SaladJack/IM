// IMAidl.aidl
package com.saladjack.im;

// Declare any non-default types here with import statements

interface IMAidl {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void signIn(String account,String password,String serverIP,int serverPort);
    void sendMessage(String message,int friendId,boolean qos);
}
