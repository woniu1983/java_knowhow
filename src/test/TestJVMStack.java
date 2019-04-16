/**
 * 
 */
package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author woniu
 *
 */
public class TestJVMStack {

	private static long count = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//		infinitelyRecursiveMethod(1);

		infinitelyThread();
	}

	public static void infinitelyRecursiveMethod(long a){
		System.out.println(count++);
		infinitelyRecursiveMethod(a);
	}

	public static void infinitelyThread(){
		// 无限制循环创建并执行线程， 看了CPU一直在93%+， JVM内存在500MB上下浮动不大, 并没有栈溢出
		long index = 0;
		while (true) {
			index++;
			//			TestRunnable runnable = new TestRunnable(index);
			TestJarCall runnable = new TestJarCall(index);
			Thread thread = new Thread(runnable);
			thread.start();
		}


	}

	public static class TestRunnable implements Runnable {

		private long id;

		public TestRunnable(long count) {
			this.id = count;
		}

		@Override
		public void run() {
			System.out.println(id);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static class TestJarCall implements Runnable {

		private long id;

		public TestJarCall(long count) {
			this.id = count;
		}

		@Override
		public void run() {
			System.out.println(id);

			Runtime run = Runtime.getRuntime();
			InputStream in = null;
			try {

				Process process = run.exec("cmd.exe /c java -jar C:\\Users\\woniu\\eclipse-workspace\\TestCase\\jar\\test.jar");

				in = process.getInputStream();

				while (in.read() != -1) {
					System.out.println(in.read());
				}

				process.waitFor();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

	}

}
