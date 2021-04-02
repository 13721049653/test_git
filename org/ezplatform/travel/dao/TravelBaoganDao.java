package org.ezplatform.travel.dao;

import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.travel.entity.TravelBaoganEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("travelBaoganDao")
public abstract interface TravelBaoganDao extends JpaBaseDao<TravelBaoganEntity, String>{
	 
	@Query(" from TravelBaoganEntity  where dqfl= ?1")
	  public abstract TravelBaoganEntity getButieByCityLevel(String CityLevel);

}
