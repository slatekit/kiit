jQuery(document).ready(function($) {

    "use strict";

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
        loop:true,
        margin:0,
        autoplay:true,
        nav:false,
        dots:false,
        fluidSpeed: true,
        center:true,
        responsive:{
            0:{
                items:1
            },
            600:{
                items:3
            },
            1000:{
                items:5
            }
        }
    });
  });