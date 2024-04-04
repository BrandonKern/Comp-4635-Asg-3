// take directly from tutorial, changed String name to int user_id for fit our assignment implementation

import java.sql.Timestamp;

public class ClientStateRecord {
	private int user_id;
	private Boolean isActive;
	private Timestamp registeredSince;
	
	ClientStateRecord(int id) {
		user_id = id;
		isActive = true;
		registeredSince = new Timestamp(System.currentTimeMillis());		
	}
	
	public synchronized int getClientName() {
		return user_id;
	}
	public synchronized void setClientName(int user_id) {
		this.user_id = user_id;
	}
	public synchronized Boolean getIsActive() {
		return isActive;
	}
	public synchronized void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
}
