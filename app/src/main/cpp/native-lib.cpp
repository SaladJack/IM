#include <jni.h>
#include <string>
#include <unistd.h>
#include <sys/file.h>
#include <stdlib.h>
#include "common.h"
extern "C"
jstring Java_com_saladjack_jnidemo_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
void start_service(char *package_name, char *service_name)
{
    /* get the sdk version */
    int version = get_version();

    pid_t pid;

    if ((pid = fork()) < 0)
    {
        exit(EXIT_SUCCESS);
    }
    else if (pid == 0)
    {
        if (package_name == NULL || service_name == NULL)
        {
            LOG_E("package name or service name is null");
            return;
        }

        char *p_name = str_stitching(package_name, "/");
        char *s_name = str_stitching(p_name, service_name);
        LOG_D("service: %s", s_name);

        if (version >= 17 || version == 0)
        {
            int ret = execlp("am", "am", "startservice",
                             "--user", "0", "-n", s_name, (char *) NULL);
            LOG_D("result %d", ret);
        }
        else
        {
            execlp("am", "am", "startservice", "-n", s_name, (char *) NULL);
        }

        LOGD(LOG_TAG , "exit start-service child process");
        exit(EXIT_SUCCESS);
    }
    else
    {
        //那么怎么样才能实现双向守护呢？
        //首先我们想到的是fork这个函数，他会创建一个子进程，
        //然后在父进程中调用waitpid()这个函数，这是一个阻塞函数，
        //父进程会一直wait到子进程挂掉，才会继续向下执行，利用这个机制
        //我们可以在主进程的c层fork一个子进程，然后父进程就可以监听到子进程的死亡，死亡的时候再重启子进程。
        waitpid(pid, NULL, 0);
    }
}

void pull_up_service_process(){
    int ret = execlp()
}

void lock_file(int fd){
    int ret_lock = flock(fd,LOCK_EX);
    if(ret_lock == 0){
        LOG_D("lock file success");
        pull_up_service_process();
    }else{
        LOG_D("lock file fail");
    }
    close(fd);
};

int main(){
    char *file_name = NULL;
    int fd = 0;
    if(access(file_name,F_OK) != -1){
        LOG_D("file exists");
        fd = open(file_name,O_RDWR);
    }else{
        LOG_D("file doesn't exist");
        fd = open(file_name,O_CREAT|O_RDWR);
    }

    if(fd == -1){
        LOG_D("open file fail, errno : %d",errno);
        exit(EXIT_FAILURE);
    }
    LOG_D("open file success");

    lock_file(fd);//主进程先锁文件

    pid_t pid = fork();
    if(pid < 0){
        LOG_D("fork fail");
        exit(EXIT_FAILURE);
    }else if(pid == 0) {
        LOG_D("this is the child process");
        lock_file(fd);
    }


    int ret_lock = flock(fd,LOCK_EX);


    if(ret_lock == 0);

}

/**
 * return fd
 */
static int open_file(char* file_name){
    int fd = 0;
    if(access(file_name,F_OK) != -1){
        fd = open(file_name,O_RDWR);
        LOG_D("file exists");
    }else{
        fd = open(file_name,O_CREAT|O_RDWR);
        LOG_D("file doesn't exist");
    }
    return fd;
}
static void close_file();

static void pull_up_service_process();

