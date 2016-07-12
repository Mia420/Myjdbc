package test.read_writeDB;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class Test1 {
	AtomicInteger i =new AtomicInteger(0);
	@Test
	public void test1(){
		while(true){
			System.out.println(i.incrementAndGet()%7);
		}
	}
}
