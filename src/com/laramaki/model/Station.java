package com.laramaki.model;

import org.orman.mapper.Model;
import org.orman.mapper.annotation.Entity;
import org.orman.mapper.annotation.PrimaryKey;

@Entity
public class Station extends Model<Station> {
	@PrimaryKey(autoIncrement=true)
	public int id;
	public int stationId;
	public String name;
	public String genre;
	public int numberOfListeners;
	public int bitrate;
	public String type;
	public String playingSong;
	public String tunein;
}
