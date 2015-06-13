package csv4j.spring.batch.jpa.domain;

import org.springframework.data.repository.CrudRepository;

public interface DomainTypeRepository extends
		CrudRepository<DomainType, Long> {

}
