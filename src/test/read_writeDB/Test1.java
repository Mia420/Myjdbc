package test.read_writeDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;

import test.Configure;

public class Test1 {
	public static void main(String[] args) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Connection conn1 = Configure.testSource.getConnection();
					System.out.println(conn1);
					Thread.sleep(10000);
					Connection conn2 = Configure.testSource.getConnection();
					System.out.println(conn2);
				} catch (Exception e) {
					e.printStackTrace();
					//System.out.println(Thread.interrupted());
				}
				System.out.println(2);
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(3000);
						System.out.println(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		System.out.println(1);
	}
}
