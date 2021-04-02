package org.ezplatform.travel.dao;

import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.travel.entity.CityEntity;
import org.ezplatform.travel.entity.TravelZhusuEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("travelZhusuDao")
public abstract interface TravelZhusuDao extends JpaBaseDao<TravelZhusuEntity, String>{
	 @Query(" from TravelZhusuEntity  where dqfl= ?1")
	  public abstract TravelZhusuEntity getTravelZhusuEntityByCityLevel(String cityName);
}
