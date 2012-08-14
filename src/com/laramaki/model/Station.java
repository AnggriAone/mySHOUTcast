package com.laramaki.model;

import org.orman.mapper.Model;
import org.orman.mapper.ModelQuery;
import org.orman.mapper.annotation.Entity;
import org.orman.mapper.annotation.PrimaryKey;
import org.orman.sql.C;
import org.orman.sql.Query;

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
	public boolean playing;
	
	public boolean isFavorite() {
		return
		Favorite.fetchSingle(ModelQuery.select().from(Favorite.class).where(C.eq("station", this.id)).getQuery(), Favorite.class) != null;
	}
	
	@Override
	public void delete() {
		Query q = ModelQuery.delete().from(Favorite.class).where(C.eq("station", id)).getQuery();
		Model.execute(q);
		super.delete();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (((Station)o).stationId == this.stationId) return true;
		return false;
	}
}
