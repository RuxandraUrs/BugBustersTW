package org.example.menuservice.service;

import lombok.RequiredArgsConstructor;
import org.example.menuservice.client.OrderServiceClient;
import org.example.menuservice.client.UserServiceClient;
import org.example.menuservice.dto.OrderResponseDto;
import org.example.menuservice.dto.UserResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Service class that aggregates data from multiple microservices for the dashboard view.
 * It demonstrates inter-service communication by fetching data from Order and User services.
 *
 * @author Ruxandra Urs - 12.01.2026
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderServiceClient orderServiceClient;
    private final UserServiceClient userServiceClient;

    /**
     * Retrieves all orders for administrative purposes by calling the Order microservice.
     *
     * @return List&lt;OrderResponseDto&gt; a list of all existing orders.
     * @author Ruxandra Urs -12.01.2026
     */
    public List<OrderResponseDto> getAllOrdersForAdmin() {
        return orderServiceClient.getAllOrders();
    }

    /**
     * Fetches orders associated with a specific client name. [cite: 10, 16]
     *
     * @param clientName the name of the client.
     * @return List&lt;OrderResponseDto&gt; the list of client orders.
     * @author Ruxandra Urs - 12.01.2026
     */
    public List<OrderResponseDto> getOrdersForClient(String clientName) {
        return orderServiceClient.getOrdersByClient(clientName);
    }

    /**
     * Searches for users in the User microservice based on a query. [cite: 10, 16]
     *
     * @param query search criteria.
     * @return List&lt;UserResponseDto&gt; matching users.
     * @author Ruxandra Urs - 12.01.2026
     */
    public List<UserResponseDto> searchUsers(String query) {
        return userServiceClient.searchUsers(query);
    }
    /**
     * Retrieves the total count of employees from the User microservice. [cite: 10, 16]
     *
     * @return Long the number of employees.
     * @author Ruxandra Urs - 12.01.2026
     */
    public Long getEmployeeCount() {
        return userServiceClient.countEmployees();
    }
}