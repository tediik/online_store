package com.jm.online_store.model.dto;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportDto {
    private Long orderNumber;
    private String userEmail;
    private String customerInitials;
    private LocalDate purchaseDate;
    private Long quantity;
    private String listOfProducts;
    private Double orderSummaryPrice;

    public static SalesReportDto orderToSalesReportDto(Order order) {
        SalesReportDto salesReportDto = new SalesReportDto();
        salesReportDto.setOrderNumber(order.getId());
        salesReportDto.setUserEmail(order.getUser().getEmail());
        salesReportDto.setCustomerInitials(createUserInitialsString(order.getUser()));
        salesReportDto.setPurchaseDate(order.getDateTime().toLocalDate());
        salesReportDto.setQuantity(order.getAmount());
        salesReportDto.setListOfProducts(order
                .getProductInOrders()
                .stream()
                .map(product -> String.valueOf(product.getProduct().getProduct() + " - (" + product.getAmount() + ")"))
                .collect(Collectors.joining( ", ")));
        salesReportDto.setOrderSummaryPrice(order.getOrderPrice());
        return salesReportDto;
    }

    /**
     * Static method that creates String with customer initials for dto
     *
     * @param user - {@link User} which made purchase
     * @return - String with initials if user have First and Last name or return "Покупатель"
     */
    private static String createUserInitialsString(User user) {
        StringBuilder userInitials = new StringBuilder();
        if (user.getFirstName() != null) {
            userInitials.append(user.getFirstName());
            if (user.getLastName() != null) {
                userInitials.append(" ");
                userInitials.append(user.getLastName());
                return userInitials.toString();
            }
            return userInitials.toString();
        }
        return "Покупатель";
    }
}
