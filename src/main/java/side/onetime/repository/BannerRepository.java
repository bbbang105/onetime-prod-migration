package side.onetime.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.Banner;
import side.onetime.domain.BarBanner;

import java.util.List;
import java.util.Optional;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    Optional<Banner> findByIdAndIsDeletedFalse(Long id);

    List<Banner> findAllByIsDeletedFalseOrderByCreatedDateDesc(Pageable pageable);

    List<Banner> findAllByIsActivatedTrueAndIsDeletedFalse();

    long countByIsDeletedFalse();
}
