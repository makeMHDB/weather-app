package lt.bit.weatherapp.repository;

import java.util.Date;
import java.util.List;
import lt.bit.weatherapp.model.Temp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TempRepository extends JpaRepository<Temp, Integer>{
    
    @Query(name = "Temp.findByObstime")
    Temp findByTime(@Param("obstime")Date date);
    
    @Query(name = "Temp.findAllSorted")
    List<Temp> findAllSorted();
    
    Temp findFirstByOrderByObstimeDesc();
}
