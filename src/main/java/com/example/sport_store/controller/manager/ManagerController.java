package com.example.sport_store.controller.manager;

import com.example.sport_store.entity.*;
import com.example.sport_store.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    private static final String UPLOAD_FOLDER = "upload/product_image/";
    final ManufacturerRepository manufacturerRepository;
    final CategoryRepository categoryRepository;
    final ColorRepository colorRepository;
    final SizeRepository sizeRepository;
    final ProductRepository productRepository;
    final ProductAttributeRepository productAttributeRepository;
    final ImageRepository imageRepository;
    final CustomerRepository customerRepository;
    final OrderRepository orderRepository;
    final OrderPositionRepository orderPositionRepository;

    @Autowired
    public ManagerController(ManufacturerRepository manufacturerRepository,
                             CategoryRepository categoryRepository,
                             ColorRepository colorRepository,
                             SizeRepository sizeRepository,
                             ProductRepository productRepository,
                             ProductAttributeRepository productAttributeRepository,
                             ImageRepository imageRepository,
                             CustomerRepository customerRepository,
                             OrderRepository orderRepository,
                             OrderPositionRepository orderPositionRepository) {
        this.manufacturerRepository = manufacturerRepository;
        this.categoryRepository = categoryRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.productRepository = productRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.imageRepository = imageRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderPositionRepository = orderPositionRepository;
    }

    /**
     * Определение объектов, которые должны быть частью модели(Model).
     */
    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        Customer customer = customerRepository.findUserByLogin(principal.getName());
        model.addAttribute("user",
                customer.getSurname() + " " + customer.getName());
    }

    /**
     * Обработка запроса - главная страница интерфейса менеджера.
     */
    @GetMapping
    public String managerPageView() {
        return "manager/manager";
    }

    /**
     * Обработка запроса - вывод списка всех категорий.
     */
    @GetMapping(value = "/categories")
    public String categoriesView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("categories", categoryRepository.findAll(PageRequest.of(page, 4)));
        model.addAttribute("currentPage", page);
        return "/manager/categories";
    }

    /**
     * Обработка запроса - сохранить категорию.
     */
    @PostMapping("/saveCategory")
    public String saveCategory(@RequestParam String id, @RequestParam String name) {
        if (categoryRepository.findCategoryByName(name).orElse(null) == null) {
            Category category = id.isEmpty() ? new Category() : categoryRepository.getCategoryById(Long.parseLong(id));
            category.setName(name);
            categoryRepository.save(category);
        }
        return "redirect:/manager/categories";
    }

    /**
     * Обработка запроса - удалить категорию.
     */
    @GetMapping("/deleteCategory")
    public String deleteCategory(Long id) {
        if (productRepository
                .findProductByCategory(categoryRepository.findCategoryById(id).orElse(null)).isEmpty()) {
            categoryRepository.deleteById(id);
        }
        return "redirect:/manager/categories";
    }

    /**
     * Обработка запроса - найти категорию по Id.
     */
    @GetMapping("/findCategory")
    @ResponseBody
    public Category findCategory(Long id) {
        return categoryRepository.getCategoryById(id);
    }

    /**
     * Обработка запроса - вывод списка всех цветов.
     */
    @GetMapping(value = "/colors")
    public String colorsView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("colors", colorRepository.findAll(PageRequest.of(page, 4)));
        model.addAttribute("currentPage", page);
        return "/manager/colors";
    }

    /**
     * Обработка запроса - сохранить цвет.
     */
    @PostMapping("/saveColor")
    public String saveColor(@RequestParam String id, @RequestParam String name) {
        if (colorRepository.findColorByName(name).orElse(null) == null) {
            Color color = id.isEmpty() ? new Color() : colorRepository.getColorById(Long.parseLong(id));
            color.setName(name);
            colorRepository.save(color);
        }
        return "redirect:/manager/colors";
    }

    /**
     * Обработка запроса - удалить цвет.
     */
    @GetMapping("/deleteColor")
    public String deleteColor(Long id) {
        if (productAttributeRepository
                .findProductAttributeByColor(colorRepository.findById(id).orElse(null)).isEmpty()) {
            colorRepository.deleteById(id);
        }
        return "redirect:/manager/colors";
    }

    /**
     * Обработка запроса - найти цвет.
     */
    @GetMapping("/findColor")
    @ResponseBody
    public Color findColor(Long id) {
        return colorRepository.getColorById(id);
    }

    /**
     * Обработка запроса - вывод списка всех брендов.
     */
    @GetMapping(value = "/manufacturers")
    public String manufacturersView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("manufacturers", manufacturerRepository.findAll(PageRequest.of(page, 4)));
        model.addAttribute("currentPage", page);
        return "/manager/manufacturers";
    }

    /**
     * Обработка запроса - сохранить бренд.
     */
    @PostMapping("/saveManufacturer")
    public String saveManufacturer(@RequestParam String id, @RequestParam String name) {
        if (manufacturerRepository.findManufacturerByName(name).orElse(null) == null) {
            Manufacturer manufacturer = id.isEmpty() ? new Manufacturer() :
                    manufacturerRepository.getManufacturerById(Long.parseLong(id));
            manufacturer.setName(name);
            manufacturerRepository.save(manufacturer);
        }
        return "redirect:/manager/manufacturers";
    }

    /**
     * Обработка запроса - удалить бренд.
     */
    @GetMapping("/deleteManufacturer")
    public String deleteManufacturer(Long id) {
        if (productRepository
                .findProductByManufacturer(manufacturerRepository.findById(id).orElse(null)).isEmpty()) {
            manufacturerRepository.deleteById(id);
        }
        return "redirect:/manager/manufacturers";
    }

    /**
     * Обработка запроса - найти бренд.
     */
    @GetMapping("/findManufacturer")
    @ResponseBody
    public Manufacturer findManufacturer(Long id) {
        return manufacturerRepository.getManufacturerById(id);
    }

    /**
     * Обработка запроса - вывод списка всех размеров.
     */
    @GetMapping(value = "/sizes")
    public String sizesView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("sizes", sizeRepository.findAll(PageRequest.of(page, 4)));
        model.addAttribute("currentPage", page);
        return "/manager/sizes";
    }

    /**
     * Обработка запроса - сохранить размер.
     */
    @PostMapping("/saveSize")
    public String saveSize(@RequestParam String id, @RequestParam String name) {
        if (sizeRepository.findSizeByName(name).orElse(null) == null) {
            Size size = id.isEmpty() ? new Size() :
                    sizeRepository.getSizeById(Long.parseLong(id));
            size.setName(name);
            sizeRepository.save(size);
        }
        return "redirect:/manager/sizes";
    }

    /**
     * Обработка запроса - удалить размер.
     */
    @GetMapping("/deleteSize")
    public String deleteSize(Long id) {
        if (productAttributeRepository
                .findProductAttributeBySize(sizeRepository.findById(id).orElse(null)).isEmpty()) {
            sizeRepository.deleteById(id);
        }
        return "redirect:/manager/sizes";
    }

    /**
     * Обработка запроса - найти размер.
     */
    @GetMapping("/findSize")
    @ResponseBody
    public Size findSize(Long id) {
        return sizeRepository.getSizeById(id);
    }

    /**
     * Обработка запроса - вывод списка всех товаров.
     */
    @GetMapping(value = "/products")
    public String productsView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("products", productRepository.findAll(PageRequest.of(page, 10)));
        model.addAttribute("currentPage", page);
        return "/manager/products";
    }

    /**
     * Обработка запроса - создать товар.
     */
    @GetMapping(value = "/createProduct")
    public String createProductPageView(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("manufacturers", manufacturerRepository.findAll());
        return "/manager/product";
    }

    /**
     * Обработка запроса - сохранить товар.
     */
    @PostMapping("/saveProduct")
    public String saveProduct(@Valid Product product,
                              BindingResult result,
                              Model model,
                              @RequestParam("files") MultipartFile[] files,
                              @RequestParam String id,
                              @RequestParam String name,
                              @RequestParam String category,
                              @RequestParam String manufacturer,
                              @RequestParam String description,
                              @RequestParam double price) {
        Product checkNameProduct = productRepository.findProductByName(name).orElse(null);
        if (checkNameProduct != null) {
            if (id.isEmpty() || (checkNameProduct.getId() != Long.parseLong(id))) {
                FieldError error = new FieldError("product", "name",
                        "Товар с таким названием уже существует");
                result.addError(error);
            }
        }
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("manufacturers", manufacturerRepository.findAll());
            return "/manager/product";
        }
        if (!id.isEmpty()) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(categoryRepository.getCategoryById(Long.parseLong(category)));
            product.setManufacturer(manufacturerRepository.getManufacturerById(Long.parseLong(manufacturer)));
        }
        productRepository.save(product);
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String newImageFileName = UUID.randomUUID().toString().replaceAll("-", "") +
                            file.getOriginalFilename();
                    Files.copy(file.getInputStream(), Paths.get(UPLOAD_FOLDER, newImageFileName));
                    Image image = new Image();
                    image.setImage(newImageFileName.replaceAll(" ", ""));
                    image.setProduct(product);
                    imageRepository.save(image);
                } catch (IOException | RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        return "redirect:/manager/products";
    }

    /**
     * Обработка запроса - удалить товар.
     */
    @GetMapping("/deleteProduct")
    public String deleteProduct(Long id) {
        Product product = productRepository.getProductById(id);
        if (orderPositionRepository.findOrderPositionByProductName(product.getName()).isEmpty()) {
            productRepository.delete(product);
        }
        return "redirect:/manager/products";
    }

    /**
     * Обработка запроса - изменить товар и добавить атрибуты.
     */
    @GetMapping("/editProduct")
    public String editProduct(Long id, Model model) {
        Product product = productRepository.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("manufacturers", manufacturerRepository.findAll());
        model.addAttribute("imagePaths", imageRepository.getImagesByProduct(product));
        model.addAttribute("image_path", "/" + UPLOAD_FOLDER);
        model.addAttribute("productAttr", productAttributeRepository.getProductAttributesByProduct(product));
        model.addAttribute("colors", colorRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        return "/manager/product_attr";
    }

    /**
     * Обработка запроса - удалить изображение товара.
     */
    @GetMapping("/deleteImage")
    public String updateProduct(Long id, HttpServletRequest request) {
        File file = new File(UPLOAD_FOLDER + imageRepository.getById(id).getImage());
        if (file.delete()) {
            imageRepository.deleteById(id);
        }
        return "redirect:" + request.getHeader("Referer");
    }

    /**
     * Обработка запроса - сохранить атрибут товара.
     */
    @PostMapping("/saveProductAttr")
    public String saveProductAttr(HttpServletRequest request,
                                  @RequestParam String color,
                                  @RequestParam String size,
                                  @RequestParam Integer quantity,
                                  @RequestParam String productId) {
        Product productAttr = productRepository.getProductById(Long.parseLong(productId));
        Color colorAttr = colorRepository.getColorById(Long.parseLong(color));
        Size sizeAttr = sizeRepository.getSizeById(Long.parseLong(size));
        ProductAttribute productAttribute = productAttributeRepository
                .findProductAttributeByProductAndColorAndSize(productAttr, colorAttr, sizeAttr).orElse(null);
        if (productAttribute == null) {
            productAttributeRepository.save(new ProductAttribute(productAttr, colorAttr, sizeAttr, quantity));
        } else {
            productAttribute.setQuantity(productAttribute.getQuantity() + quantity);
            productAttributeRepository.save(productAttribute);
        }
        return "redirect:" + request.getHeader("Referer");
    }

    /**
     * Обработка запроса - вывести отчет о продажах.
     */
    @GetMapping("/reports")
    public String reportsPageView(Model model) {
        List<Order> orderListToday =
                orderRepository
                        .findOrdersByPaymentTypeIsNotNullAndOrderCloseDateIsAfter(
                                Timestamp.valueOf(LocalDate.now().atStartOfDay()));

        List<Order> orderListWeek =
                orderRepository
                        .findOrdersByPaymentTypeIsNotNullAndOrderCloseDateIsAfter(
                                Timestamp.valueOf(LocalDate.now().with(
                                        WeekFields.of(Locale.getDefault()).getFirstDayOfWeek()).atStartOfDay()));

        List<Order> orderListMonth =
                orderRepository
                        .findOrdersByPaymentTypeIsNotNullAndOrderCloseDateIsAfter(
                                Timestamp.valueOf(LocalDate.now().withDayOfMonth(1).atStartOfDay()));

        List<Order> orderListYear =
                orderRepository
                        .findOrdersByPaymentTypeIsNotNullAndOrderCloseDateIsAfter(
                                Timestamp.valueOf(LocalDate.now().withDayOfYear(1).atStartOfDay()));

        model.addAttribute("todayAllOrdersCount", orderListToday.size());
        model.addAttribute("paymentTypeCashSumToday", sumCash(orderListToday, 1L));
        model.addAttribute("paymentTypeCardSumToday", sumCash(orderListToday, 2L));
        model.addAttribute("totalSumToday", sumCash(orderListToday, 0L));

        //week
        model.addAttribute("weekAllOrdersCount", orderListWeek.size());
        model.addAttribute("paymentTypeCashSumWeek", sumCash(orderListWeek, 1L));
        model.addAttribute("paymentTypeCardSumWeek", sumCash(orderListWeek, 2L));
        model.addAttribute("totalSumWeek", sumCash(orderListWeek, 0L));

        //month
        model.addAttribute("monthAllOrdersCount", orderListMonth.size());
        model.addAttribute("paymentTypeCashSumMonth", sumCash(orderListMonth, 1L));
        model.addAttribute("paymentTypeCardSumMonth", sumCash(orderListMonth, 2L));
        model.addAttribute("totalSumMonth", sumCash(orderListMonth, 0L));

        //year
        model.addAttribute("yearAllOrdersCount", orderListYear.size());
        model.addAttribute("paymentTypeCashSumYear", sumCash(orderListYear, 1L));
        model.addAttribute("paymentTypeCardSumYear", sumCash(orderListYear, 2L));
        model.addAttribute("totalSumYear", sumCash(orderListYear, 0L));
        return "manager/reports";
    }

    /**
     * Метод расчета суммы продаж в зависимости от типа оплаты.
     * 0 - по всему списку, > 0 - выбор из списка записи с определенным Id типа оплаты.
     */
    private double sumCash(List<Order> orderList, Long paymentId) {
        return paymentId.equals(0L) ? orderList.stream().mapToDouble(Order::getOrderSum).sum() :
                orderList.stream().filter(order -> order.getPaymentType().getId().equals(paymentId))
                        .mapToDouble(Order::getOrderSum).sum();
    }
}
