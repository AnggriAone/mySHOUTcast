package com.laramaki.model;

import org.orman.mapper.Model;
import org.orman.mapper.annotation.Entity;
import org.orman.mapper.annotation.PrimaryKey;

@Entity
public class Favorite extends Model<Favorite> {
	
	@PrimaryKey(autoIncrement=true)
	public int id;
	public Station station;
}
