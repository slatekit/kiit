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
    
    window.addEventListener('scroll', function (e) {
        var nav = document.getElementById('nav');
        if (document.documentElement.scrollTop || document.body.scrollTop > window.innerHeight) {
            $(".header-classic").css({"background-color":"#4e65c3"});  
            } else {
                $(".header-classic").css({"background-color":"transparent"});
            }
    });
    

  });