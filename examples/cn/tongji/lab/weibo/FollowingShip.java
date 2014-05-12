package cn.tongji.lab.weibo;

import weibo4j.model.User;

public class FollowingShip {

	private String sourceId;
	private String targetId;

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FollowingShip other = (FollowingShip) obj;
		if (this.sourceId == other.sourceId && this.targetId == other.targetId) {
			return true;
		} else
			return false;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public FollowingShip(String sourceId, String targetId) {
		super();
		// TODO Auto-generated constructor stub
		this.sourceId = sourceId;
		this.targetId = targetId;
	}

}
