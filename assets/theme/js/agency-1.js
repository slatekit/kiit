jQuery(document).ready(function ($) {

    // Slick Slider
    $(document).ready(function () {
        $('.your-class').slick({
            infinite: true,
            slidesToShow: 5,
            slidesToScroll: 1,
            rrows: false,
            dots: false,
            autoplay: true,
            autoplaySpeed: 2000,
            responsive: [
                {
                  breakpoint: 768,
                  settings: {
                    arrows: false,
                    slidesToShow: 2
                  }
                },
                {
                  breakpoint: 480,
                  settings: {
                    arrows: false,
                    slidesToShow: 1
                  }
                }
              ]
        });
    });


    //Wow JS
    new WOW().init({
      mobile: true,
      });
  
      // Navbar
  
      $('.dropdown-menu a.dropdown-toggle').on('click', function (e) {
          if (!$(this).next().hasClass('show')) {
              $(this).parents('.dropdown-menu').first().find('.show').removeClass("show");
          }
          var $subMenu = $(this).next(".dropdown-menu");
          $subMenu.toggleClass('show');
  
          $(this).parents('li.nav-item.dropdown.show').on('hidden.bs.dropdown', function (e) {
              $('.dropdown-submenu .show').removeClass("show");
          });
  
          return false;
      });
  
      // Animate loader off screen
      $('.se-pre-con').fadeOut('slow');
      $('.number').counterUp({
        delay: 10,
        time: 1000,
        triggerOnce:true
      });

    // Owl Carousel
    $('.owl-carousel').owlCarousel({
        loop: true,
        margin: 10,
        autoplay: true,
        nav: false,
        dots: false,
        fluidSpeed: true,
        responsive: {
            0: {
                items: 1
            },
            600: {
                items: 2
            },
            1000: {
                items: 4
            }
        }
    });
});