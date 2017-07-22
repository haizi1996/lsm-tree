package lsm.util;

public class Result<V> {
	
	private boolean result;
	private V message ;
	public Result(boolean result, V message) {
		this.result = result;
		this.message = message;
	}
	public V getMessage() {
		return message;
	}
	public void setMessage(V message) {
		this.message = message;
	}
	public boolean isResult() {
		return result;
	}
	

}
