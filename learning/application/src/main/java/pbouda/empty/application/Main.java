package pbouda.empty.application;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Started - PID: " + ProcessHandle.current().pid());
        Thread.currentThread().join();
    }
}
