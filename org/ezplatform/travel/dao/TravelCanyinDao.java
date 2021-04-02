package org.ezplatform.travel.dao;

import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.travel.entity.TravelCanyinEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("travelCanyinDao")
public abstract interface TravelCanyinDao extends JpaBaseDao<TravelCanyinEntity, String>{
	 @Query(" from TravelCanyinEntity  where dqfl= ?1")
	  public abstract TravelCanyinEntity getButieByCityLevel(String CityLevel);

	 @Query(nativeQuery=true, value="select a.zhongcanbz from ygbxfbgbt a left join  csdjhf b on a.dqfl=b.csjb where b.mc=?1 ")
	  public abstract String  getCanyinjine(String cityName);
	 
}
