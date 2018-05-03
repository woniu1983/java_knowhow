/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/** 
 * @ClassName: TestThreadPool <br/> 
 * @Description: 线程池 ThreadPoolExecutor Sample <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月3日 下午7:15:58 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class TestThreadPool {
	
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

	/**  
	 * @Title: main  
	 * @Description: TODO  
	 *
	 * @param args 
	*/
	public static void main(String[] args) {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 4, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		executor.allowCoreThreadTimeOut(true);	
		
		for (int i = 1; i <= 10; i++) {
			TaskRunnable runn = new TaskRunnable(i);
			executor.execute(runn);
		}
		System.out.println("=====================================");
		
		while(executor.getActiveCount() > 0) {
			
			System.out.println(">>>>>>>>>>>>>>>>> 正在执行中， 请稍候----");
			System.out.println("executor.getActiveCount()=" + executor.getActiveCount());
			System.out.println("executor.getTaskCount()=" + executor.getTaskCount());
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
			}
		}
		
		try {
			System.out.println("!!!!!!!!!!!!!!!!!! 全部执行完毕， 准备关闭操作----");
			System.out.println("!!!!!!!!!!!!!!!!!!executor.getActiveCount()=" + executor.getActiveCount());
			System.out.println("!!!!!!!!!!!!!!!!!!executor.getTaskCount()=" + executor.getTaskCount());
			executor.shutdown();
			System.out.println("!!!!!!!!!!!!!!!!!!executor.isShutdown()=" + executor.isShutdown());
		} catch (Exception e) {
			
		}
		
	}
	
	private static class TaskRunnable implements Runnable {
		
		private int id = 0;
		
		public TaskRunnable(int id) {
			this.id = id;
		}
		

		@Override
		public void run() {
			String date = format.format(new Date());
			System.out.println(Thread.currentThread().getName() + " -->[" + id + "]" + ">>>Start --------" + date);
			
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			date = format.format(new Date());
			System.out.println(Thread.currentThread().getName() + " -->[" + id + "]" + "###End --------" + date);
			
		}
		
	}

}
