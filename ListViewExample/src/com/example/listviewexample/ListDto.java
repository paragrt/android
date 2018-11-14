/**
 * 
 */
package com.example.listviewexample;

/**
 * @author Vienna
 * 
 */
public class ListDto {
	private int id;
	private String name;

	/**
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("%d\t%s", id, name);
	}
}
