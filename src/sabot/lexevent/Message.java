package sabot.lexevent;

public class Message implements Cloneable {
	private String contentType = null;
	private String content = null;

	public enum ContentType {
		PlainText("PlainText"), SMML("SMML");

		private String value;

		private ContentType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}
	}

	@Override
	public Message clone() {

		Message clone = new Message();
		try {
			clone = (Message) super.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clone;
	}

	public Message(ContentType contentType, String content) {
		this.contentType = contentType.toString();
		this.content = content;
	}

	public Message() {
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType.toString();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
