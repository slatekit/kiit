/*global $, window, document, setTimeout, WOW*/
$(document).ready(function () {

    'use strict';

    var scrollTopButton  = $('#scrollTop'),
        scrollableAnchor = $('.link-scroll'),
        preloader        = $('.preloader'),
        body             = $('body');


    // ------------------------------------------------------- //
    // Add Text Background from HTML [data-text] attribute
    // ------------------------------------------------------- //
    $('.has-background-text').each(function () {

        $('<span class="text-bg"></span>').prependTo(this);
        var textBackground = $(this).attr('data-text');
        $(this).find('.text-bg').text(textBackground);

    });


    $('.has-background-text-gray').each(function () {

        $('<span class="text-bg-gray"></span>').prependTo(this);
        var textBackground = $(this).attr('data-text');
        $(this).find('.text-bg-gray').text(textBackground);

    });


    $('.with-bg-text').each(function () {

        $('<span class="heading-bg-text"></span>').prependTo(this);
        var textBackground = $(this).attr('data-text');
        $(this).find('.heading-bg-text').text(textBackground);

    });


    // ------------------------------------------------------------------- //
    // Move to the top of the page on clicking the scroll top button
    // ------------------------------------------------------------------- //
    scrollTopButton.on('click', function () {
        body.animate({ scrollTop: 0}, 1000);
    });


    // ------------------------------------------------------------------- //
    // Screens Slider
    // ------------------------------------------------------------------ //
    $('.screens-slider').owlCarousel({
        loop: true,
        margin: 20,
        dots: true,
        nav: true,
        smartSpeed: 700,
        navText: [
            "<i class='fa fa-angle-left'></i>",
            "<i class='fa fa-angle-right'></i>"
        ],
        responsiveClass: true,
        responsive: {
            0: {
                items: 1,
                nav: true
            },
            600: {
                items: 1,
                nav: true
            },
            1000: {
                items: 1,
                nav: true,
                loop: true
            }
        }
    });


    // ------------------------------------------------------------------- //
    // Testimonials Slider
    // ------------------------------------------------------------------ //
    $('.testimonials-slider').owlCarousel({
        loop: true,
        margin: 20,
        dots: true,
        nav: true,
        smartSpeed: 700,
        navText: [
            "<i class='fa fa-long-arrow-left'></i>",
            "<i class='fa fa-long-arrow-right'></i>"
        ],
        responsiveClass: true,
        responsive: {
            0: {
                items: 1,
                nav: true
            },
            600: {
                items: 1,
                nav: true
            },
            1000: {
                items: 1,
                nav: true,
                loop: false
            }
        }
    });


    // ---------------------------------------------------------- //
    // Navbar Background Changinig & Hiding on Scroll
    // ---------------------------------------------------------- //
    var c, currentScrollTop = 0,
        navbar = $('.navbar');

    $(window).on('scroll', function () {

        // Navbar change background on scroll
        if ($(this).scrollTop() >= 5) {
            navbar.addClass('active');
        } else {
            navbar.removeClass('active');
        }

        // Navbar auto hide on scroll down functionality
        var a = $(window).scrollTop(), b = navbar.height();

        currentScrollTop = a;
        if (c < currentScrollTop && a > b + b) {
            navbar.addClass("scrollUp");
        } else if (c > currentScrollTop && !(a <= b)) {
            navbar.removeClass("scrollUp");
        }
        c = currentScrollTop;


        // Hide/Show scroll to top button at a certain place [> 2000 px from the top]
        if ($(window).scrollTop() >= 2000) {
            scrollTopButton.addClass('active');
        } else {
            scrollTopButton.removeClass('active');
        }
    });


    // ---------------------------------------------------------- //
    // Preventing URL update on navigation link click
    // ---------------------------------------------------------- //
    scrollableAnchor.on('click', function (e) {
        var anchor = $(this);
        $('html, body').stop().animate({
            scrollTop: $(anchor.attr('href')).offset().top
        }, 1000);
        e.preventDefault();
    });


    // ---------------------------------------------------------- //
    // Scroll Spy
    // ---------------------------------------------------------- //
    body.scrollspy({
        target: '#navbarcollapse',
        offset: 80
    });


    // ----------------------------------------------------------- //
    // WOW.Js Initialization
    // ----------------------------------------------------------- //
    new WOW().init();


    // ---------------------------------------------------------- //
    // Preloader
    // ---------------------------------------------------------- //
    $(window).on('load', function () {
        preloader.fadeOut(500, function () {
            $(this).remove();
        });
    });

});
