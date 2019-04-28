package cn.woniu.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

public class ProgressEntity extends HttpEntityWrapper {

	private ProgressListener listener;

	private long totalSize;

	public ProgressEntity(final HttpEntity wrapped, final ProgressListener listener, long totalSize) {
		super(wrapped);
		this.listener = listener;
		this.totalSize = totalSize;
	}

	@Override
	public void writeTo(final OutputStream out) throws IOException {
		this.wrappedEntity.writeTo(out instanceof ProgressOutputStream ? out
				: new ProgressOutputStream(out, this.listener, this.totalSize));
	}


	public static class ProgressOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;
		private long totalSize;

		private int progress = 0;

		public ProgressOutputStream(final OutputStream out, final ProgressListener listener, long totalSize) {
			super(out);
			this.listener = listener;
			this.totalSize = totalSize;
			this.transferred = 0;
		}

		@Override
		public void write(final byte[] b, final int off, final int len) throws IOException {
			out.write(b, off, len);

			this.transferred += len;
			updateProgress();
		}

		@Override
		public void write(final int b) throws IOException {
			out.write(b);

			this.transferred++;
			updateProgress();

		}

		private void updateProgress() {
			if (this.totalSize > 0) {
				int newProgress = (int)(this.transferred * 100 / this.totalSize);
				if (newProgress > this.progress) {
					this.progress = newProgress;
					if (this.listener != null) {
						this.listener.onProgressUpdate(this.progress);
					}
				}
			}
		}

	}
}
