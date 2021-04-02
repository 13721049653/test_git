package org.ezplatform.travel.dao;

import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.travel.entity.CityEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("cityEntityDao")
public abstract interface CityEntityDao extends JpaBaseDao<CityEntity, String>{
	
	  @Query(" from CityEntity  where name= ?1")
	  public abstract CityEntity getCityEntityByCityName(String cityName);

	
}
