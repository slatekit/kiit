(function($) {
  "use strict";
  // Add "loaded" class when a section has been loaded
  $(window).scroll(function() {
    var scrollTop = $(window).scrollTop();
    $(".section").each(function() {
      var elementTop = $(this).offset().top - $('#header').outerHeight();
      if(scrollTop >= elementTop) {
        $(this).addClass('loaded');
      }
    });
  });

  // One Page Navigation Setup
  $('.one-page-nav #navigation').singlePageNav({
    offset: $('.one-page-nav').outerHeight(),
    filter: ':not(.external)',
    speed: 750,
    currentClass: 'active',

    beforeStart: function() {
    },
    onComplete: function() {
    }
  });

  // Sticky Navbar Affix
  $('.one-page-nav').affix({
    offset: {
      top: $('#topbar').outerHeight(),
    }
  });

  // Smooth Hash Link Scroll
  $('.smooth-scroll').click(function() {
    if (location.pathname.replace(/^\//,'') === this.pathname.replace(/^\//,'') && location.hostname === this.hostname) {

      var target = $(this.hash);
      target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
      if (target.length) {
        $('html,body').animate({
          scrollTop: target.offset().top
        }, 1000);
        return false;
      }
    }
  });

  $('.nav a').on('click', function(){
    if($('.navbar-toggle').css('display') !=='none'){
      $(".navbar-toggle").click();
    }
  });

  var $container = $('.portfolio-isotope');
  $container.imagesLoaded(function(){
    $container.isotope({
      itemSelector : '.portfolio-item',
      resizable: true,
      resizesContainer: true
    });
  });

  // filter items when filter link is clicked
  $('#filters a').click(function(){
    var selector = $(this).attr('data-filter');
    $container.isotope({ filter: selector });
    return false;
  });

  $('#contactform').submit(function() {
    var action = $(this).attr('action');
    var values = $(this).serialize();

    $.post(action, values, function(data) {
      
      $('.results').hide().html(data).slideDown('slow');

      $('#contactform').find('.form-control').val('');
      //$('.main-action').slideDown('slow');
      //$('.main-action').slideDown('slow');
      //$('#submit').removeAttr('disabled');
      /*if (data.match('success') !== null) {
        $('#contactform').slideUp('slow');
      }*/
    });

    /*$(".main-action").slideUp(750, function() {
      $('.main-action').hide();
      $.post(action, values, function(data) {
        $('.main-action').html(data);
        $('.main-action').slideDown('slow');
        $('#submit').removeAttr('disabled');
        if (data.match('success') !== null) {
          $('#contactform').slideUp('slow');
        }
      });
    });*/
    return false;

  });
})(jQuery);