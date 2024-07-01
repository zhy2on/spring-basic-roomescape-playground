package roomescape.theme;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ThemeService {
    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public Theme createTheme(Theme theme) {
        return themeRepository.save(theme);
    }

    public List<Theme> findAll() {
        return themeRepository.findAll();
    }

    public void deleteById(Long id) {
        themeRepository.deleteById(id);
    }
}
