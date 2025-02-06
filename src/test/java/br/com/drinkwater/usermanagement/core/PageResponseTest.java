package br.com.drinkwater.usermanagement.core;

import br.com.drinkwater.core.PageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class PageResponseTest {

    @Test
    public void givenPageObject_whenConvertingToPageResponse_thenShouldReturnCorrectPageResponse() {
        // Arrange
        var content = List.of("Item 1", "Item 2", "Item 3");
        var pageable = PageRequest.of(0, 3);
        var totalElements = 5L;
        Page<String> page = new PageImpl<>(content, pageable, totalElements);

        // Act
        var sut = PageResponse.of(page);

        // Assert
        assertThat(sut).isNotNull();
        assertThat(sut.content()).isEqualTo(content);
        assertThat(sut.totalElements()).isEqualTo(totalElements);
        assertThat(sut.totalPages()).isEqualTo(2);
        assertThat(sut.pageSize()).isEqualTo(3);
        assertThat(sut.pageNumber()).isEqualTo(0);
        assertThat(sut.first()).isTrue();
        assertThat(sut.last()).isFalse();
    }
}