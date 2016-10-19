package ledger.controller.register;


import org.junit.Before;

import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by gert on 10/18/16.
 */
public class TaskTest {

    //private boolean blockingBool;
    private Bar bar;
    private Foo foo;
    @Before
    public void setAllFalse(){
        bar = new Bar();
        foo = new Foo();
    }
    @Test(timeout = 3000)
    public void testNoArgsReturn(){
        TaskWithReturn<Boolean> t = new TaskWithReturn<Boolean>(bar::call);


        t.startTask();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e){
            System.out.println(e.getStackTrace());
        }
        assertTrue(bar.getTest1());
        assertFalse(bar.getTest2());
        bar.setBlockingBool

(true);
        //bar.setTrue();
        assertFalse(t.waitForResult());
        assertTrue(bar.getTest2());
    }
    @Test(timeout = 3000)
    public void testNoArgsReturnSuccessCall(){
        TaskWithReturn<Boolean> t = new TaskWithReturn<Boolean>(bar::call);
        t.RegisterSuccessEvent(bar::setBlockingBool);
        t.startTask();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e){
            System.out.println(e.getStackTrace());
        }
        //bar.setBlockingBool(true);
        bar.setTrue();
        assertFalse(t.waitForResult());
        assertFalse(bar.getBlock());
    }
    @Test(timeout = 3000)
    public void testNoArgsReturnFailureCall(){
        TaskWithReturn<Boolean> t = new TaskWithReturn<>(bar::fail);
        t.RegisterFailureEvent(bar::handleFail);
        t.startTask();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e){
            System.out.println(e.getStackTrace());
        }
        t.waitForComplete();
        assertTrue(bar.getBlock());
    }
    @Test(timeout = 3000)
    public void testArgsNoReturn(){
        TaskWithArgs<Boolean> t = new TaskWithArgs<Boolean>(foo::call, true);
        t.startTask();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e){
            System.out.println(e.getStackTrace());
        }
        assertTrue(foo.isStartBool());
        assertFalse(foo.isFinishBool());
        foo.setBlockingBool(true);
        t.waitForComplete();
        assertTrue(foo.isFinishBool());
    }
    @Test(timeout = 3000)
    public void testArgsNoReturnSuccessCall(){
        TaskWithArgs<Boolean> t = new TaskWithArgs<Boolean>(foo::call, true);
        t.RegisterSuccessEvent(foo::successCall);
        t.startTask();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e){
            System.out.println(e.getStackTrace());
        }
        foo.setBlockingBool(true);
        t.waitForComplete();
        assertTrue(foo.isFinishBool());
        assertTrue(foo.isSuccess());
    }
    @Test(timeout = 3000)
    public void atestArgsNoReturnFailureCall(){
        TaskWithArgs<Boolean> t = new TaskWithArgs<Boolean>(foo::fail, true);
        t.RegisterFailureEvent(foo::handleFail);
        t.startTask();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e){
            System.out.println(e.getStackTrace());
        }
        t.waitForComplete();
        assertTrue(foo.isSuccess());
    }

    private class Bar {
        private boolean testBool1;
        private boolean testBool2;
        private boolean blockingBool;
        public Bar (){
            blockingBool = false;
            testBool1 = false;
            testBool2 = false;
        }
        public boolean call(){
            testBool1 = true;
            while(!getBlock()){

            }
            testBool2 = true;
            return false;
        }
        public  synchronized boolean setTrue(){
            blockingBool = true;
            return blockingBool;
        }
        public synchronized void setBlockingBool(boolean bool){
            blockingBool = bool;
        }
        public boolean fail() throws Exception{
            throw new Exception();

        }
        public void handleFail(Exception e){
            setBlockingBool(true);
        }
        public boolean getTest1(){
            return testBool1;
        }
        public boolean getTest2(){
            return testBool2;
        }
        public synchronized boolean getBlock() {return blockingBool;}

    }
    public class Foo {
        private boolean finishBool;
        private boolean startBool;
        private boolean blockingBool;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        private boolean success;
        public Foo(){
            finishBool = false;
            startBool = false;
            blockingBool =false;
            success = false;
        }
        public void call(boolean bool){
            setStartBool(true);
            while(!isBlockingBool());
            setFinishBool(true);
        }
        public void successCall() {
            success = true;
        }
        public boolean isFinishBool() {
            return finishBool;
        }

        public void setFinishBool(boolean finishBool) {
            this.finishBool = finishBool;
        }

        public boolean isStartBool() {
            return startBool;
        }

        public void setStartBool(boolean startBool) {
            this.startBool = startBool;
        }

        public synchronized boolean isBlockingBool() {
            return blockingBool;
        }

        public synchronized void setBlockingBool(boolean blockingBool) {
            this.blockingBool = blockingBool;
        }
        public void fail(boolean t) throws Exception{
            throw  new Exception();
        }
        public void handleFail(Exception e){
            successCall();
        }

    }
}
