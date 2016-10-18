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
    @Before
    public void setAllFalse(){

        //blockingBool = false;
        bar = new Bar();
    }
    @Test(timeout = 3000)
    public void testNoArgsReturn(){
        Task t = new Task<Object, Boolean>(bar::call);
        t.startTask();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e){
            System.out.println(e.getStackTrace());
        }
        assertTrue(bar.getTest1());
        assertFalse(bar.getTest2());
        bar.setTrue();
        t.waitForComplete();
        assertTrue(bar.getTest2());
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
            while(!blockingBool){
            }
            testBool2 = true;
            return true;
        }
        public void setTrue(){
            blockingBool = true;
        }
        public boolean getTest1(){
            return testBool1;
        }
        public boolean getTest2(){
            return testBool2;
        }
    }
}
