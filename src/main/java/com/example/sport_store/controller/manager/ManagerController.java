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
    @Autowired
    ManufacturerRepository manufacturerRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ColorRepository colorRepository;
    @Autowired
    SizeRepository sizeRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductAttributeRepository productAttributeRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OrderRepository orderRepository;

    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        Customer customer = customerRepository.findUserByLogin(principal.getName());
        model.addAttribute("user",
                customer.getSurname() + " " + customer.getName());
    }

    @GetMapping
    public String managerPage() {
        return "manager/manager";
    }

    @GetMapping(value = "/categories")
    public String categoriesView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("categories", categoryRepository.findAll(PageRequest.of(page, 4)));
        model.addAttribute("currentPage", page);
        return "/manager/categories";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@RequestParam String id, @RequestParam String name) {
        if (categoryRepository.findCategoryByName(name).orElse(null) == null) {
            Category category = id.isEmpty() ? new Category() : categoryRepository.getCategoryById(Long.parseLong(id));
            category.setName(name);
            categoryRepository.save(category);
        }
        return "redirect:/manager/categories";
    }

    @GetMapping("/deleteCategory")
    public String deleteCategory(Long id) {
        if (productRepository
                .findProductByCategory(categoryRepository.findCategoryById(id).orElse(null)).isEmpty()) {
            categoryRepository.deleteById(id);
        }
        return "redirect:/manager/categories";
    }

    @GetMapping("/findCategory")
    @ResponseBody
    public Category findCategory(Long id) {
        return categoryRepository.getCategoryById(id);
    }

    @GetMapping(value = "/colors")
    public String colorsView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("colors", colorRepository.findAll(PageRequest.of(page, 4)));
        model.addAttribute("currentPage", page);
        return "/manager/colors";
    }

    @PostMapping("/saveColor")
    public String saveColor(@RequestParam String id, @RequestParam String name) {
        if (colorRepository.findColorByName(name).orElse(null) == null) {
            Color color = id.isEmpty() ? new Color() : colorRepository.getColorById(Long.parseLong(id));
            color.setName(name);
            colorRepository.save(color);
        }
        return "redirect:/manager/colors";
    }

    @GetMapping("/deleteColor")
    public String deleteColor(Long id) {
        if (productAttributeRepository
                .findProductAttributeByColor(colorRepository.findById(id).orElse(null)).isEmpty()) {
            colorRepository.deleteById(id);
        }
        return "redirect:/manager/colors";
    }

    @GetMapping("/findColor")
    @ResponseBody
    public Color findColor(Long id) {
        return colorRepository.getColorById(id);
    }

    @GetMapping(value = "/manufacturers")
    public String manufacturersView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("manufacturers", manufacturerRepository.findAll(PageRequest.of(page, 4)));
        model.addAttribute("currentPage", page);
        return "/manager/manufacturers";
    }

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

    @GetMapping("/deleteManufacturer")
    public String deleteManufacturer(Long id) {
        if (productRepository
                .findProductByManufacturer(manufacturerRepository.findById(id).orElse(null)).isEmpty()) {
            manufacturerRepository.deleteById(id);
        }
        return "redirect:/manager/manufacturers";
    }

    @GetMapping("/findManufacturer")
    @ResponseBody
    public Manufacturer findManufacturer(Long id) {
        return manufacturerRepository.getManufacturerById(id);
    }


    @GetMapping(value = "/sizes")
    public String sizesView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("sizes", sizeRepository.findAll(PageRequest.of(page, 4)));
        model.addAttribute("currentPage", page);
        return "/manager/sizes";
    }

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

    @GetMapping("/deleteSize")
    public String deleteSize(Long id) {
        if (productAttributeRepository
                .findProductAttributeBySize(sizeRepository.findById(id).orElse(null)).isEmpty()) {
            sizeRepository.deleteById(id);
        }
        return "redirect:/manager/sizes";
    }

    @GetMapping("/findSize")
    @ResponseBody
    public Size findSize(Long id) {
        return sizeRepository.getSizeById(id);
    }

    @GetMapping(value = "/products")
    public String productsView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("products", productRepository.findAll(PageRequest.of(page, 10)));
        model.addAttribute("currentPage", page);
        return "/manager/products";
    }

    @GetMapping(value = "/createProduct")
    public String createProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("manufacturers", manufacturerRepository.findAll());
        return "/manager/product";
    }

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

    @GetMapping("/deleteProduct")
    public String deleteProduct(Long id) {
        productRepository.deleteById(id);
        return "redirect:/manager/products";
    }

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

    @GetMapping("/deleteImage")
    public String updateProduct(Long id, HttpServletRequest request) {
        File file = new File(UPLOAD_FOLDER + imageRepository.getById(id).getImage());
        if (file.delete()) {
            imageRepository.deleteById(id);
        }
        return "redirect:" + request.getHeader("Referer");
    }

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

    @GetMapping("/reports")
    public String reportsView(Model model) {
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

        double sumToday = orderListToday.stream().mapToDouble(Order::getOrderSum).sum();
        double sumCashToday = orderListToday.stream().filter(order -> order.getPaymentType().getId() == 1L).mapToDouble(Order::getOrderSum).sum();
        double sumCardToday = orderListToday.stream().filter(order -> order.getPaymentType().getId() == 2L).mapToDouble(Order::getOrderSum).sum();
        model.addAttribute("todayAllOrdersCount", orderListToday.size());
        model.addAttribute("paymentTypeCashSumToday", sumCashToday);
        model.addAttribute("paymentTypeCardSumToday", sumCardToday);
        model.addAttribute("totalSumToday", sumToday);

        //week
        double sumWeek = orderListWeek.stream().mapToDouble(Order::getOrderSum).sum();
        double sumCashWeek = orderListWeek.stream().filter(order -> order.getPaymentType().getId() == 1L).mapToDouble(Order::getOrderSum).sum();
        double sumCardWeek = orderListWeek.stream().filter(order -> order.getPaymentType().getId() == 2L).mapToDouble(Order::getOrderSum).sum();
        model.addAttribute("weekAllOrdersCount", orderListWeek.size());
        model.addAttribute("paymentTypeCashSumWeek", sumCashWeek);
        model.addAttribute("paymentTypeCardSumWeek", sumCardWeek);
        model.addAttribute("totalSumWeek", sumWeek);

        //month
        double sumMonth = orderListMonth.stream().mapToDouble(Order::getOrderSum).sum();
        double sumCashMonth = orderListMonth.stream().filter(order -> order.getPaymentType().getId() == 1L).mapToDouble(Order::getOrderSum).sum();
        double sumCardMonth = orderListMonth.stream().filter(order -> order.getPaymentType().getId() == 2L).mapToDouble(Order::getOrderSum).sum();
        model.addAttribute("monthAllOrdersCount", orderListMonth.size());
        model.addAttribute("paymentTypeCashSumMonth", sumCashMonth);
        model.addAttribute("paymentTypeCardSumMonth", sumCardMonth);
        model.addAttribute("totalSumMonth", sumMonth);

        //year
        double sumYear = orderListYear.stream().mapToDouble(Order::getOrderSum).sum();
        double sumCashYear = orderListYear.stream().filter(order -> order.getPaymentType().getId() == 1L).mapToDouble(Order::getOrderSum).sum();
        double sumCardYear = orderListYear.stream().filter(order -> order.getPaymentType().getId() == 2L).mapToDouble(Order::getOrderSum).sum();
        model.addAttribute("yearAllOrdersCount", orderListYear.size());
        model.addAttribute("paymentTypeCashSumYear", sumCashYear);
        model.addAttribute("paymentTypeCardSumYear", sumCardYear);
        model.addAttribute("totalSumYear", sumYear);
        return "manager/reports";
    }
}
