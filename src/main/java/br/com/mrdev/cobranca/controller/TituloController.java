package br.com.mrdev.cobranca.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.mrdev.cobranca.model.StatusTitulo;
import br.com.mrdev.cobranca.model.Titulo;
import br.com.mrdev.cobranca.repository.Titulos;
import br.com.mrdev.cobranca.repository.filter.TituloFilter;
import br.com.mrdev.cobranca.service.CadastroTituloService;

//Created by:
//
//	MMMMMMMM               MMMMMMMMRRRRRRRRRRRRRRRRR           	DDDDDDDDDDDDD      EEEEEEEEEEEEEEEEEEEEEEVVVVVVVV           VVVVVVVV
//	M:::::::M             M:::::::MR::::::::::::::::R          	D::::::::::::DDD   E::::::::::::::::::::EV::::::V           V::::::V
//	M::::::::M           M::::::::MR::::::RRRRRR:::::R         	D:::::::::::::::DD E::::::::::::::::::::EV::::::V           V::::::V
//	M:::::::::M         M:::::::::MRR:::::R     R:::::R        	DDD:::::DDDDD:::::DEE::::::EEEEEEEEE::::EV::::::V           V::::::V
//	M::::::::::M       M::::::::::M  R::::R     R:::::R        	  D:::::D    D:::::D E:::::E       EEEEEE V:::::V           V:::::V 
//	M:::::::::::M     M:::::::::::M  R::::R     R:::::R        	  D:::::D     D:::::DE:::::E               V:::::V         V:::::V  
//	M:::::::M::::M   M::::M:::::::M  R::::RRRRRR:::::R         	  D:::::D     D:::::DE::::::EEEEEEEEEE      V:::::V       V:::::V   
//	M::::::M M::::M M::::M M::::::M  R:::::::::::::RR          	  D:::::D     D:::::DE:::::::::::::::E       V:::::V     V:::::V    
//	M::::::M  M::::M::::M  M::::::M  R::::RRRRRR:::::R         	  D:::::D     D:::::DE:::::::::::::::E        V:::::V   V:::::V     
//	M::::::M   M:::::::M   M::::::M  R::::R     R:::::R        	  D:::::D     D:::::DE::::::EEEEEEEEEE         V:::::V V:::::V      
//	M::::::M    M:::::M    M::::::M  R::::R     R:::::R        	  D:::::D     D:::::DE:::::E                    V:::::V:::::V       
//	M::::::M     MMMMM     M::::::M  R::::R     R:::::R        	  D:::::D    D:::::D E:::::E       EEEEEE        V:::::::::V        
//	M::::::M               M::::::MRR:::::R     R:::::R        	DDD:::::DDDDD:::::DEE::::::EEEEEEEE:::::E         V:::::::V         
//	M::::::M               M::::::MR::::::R     R:::::R ...... 	D:::::::::::::::DD E::::::::::::::::::::E          V:::::V          
//	M::::::M               M::::::MR::::::R     R:::::R .::::. 	D::::::::::::DDD   E::::::::::::::::::::E           V:::V           
//	MMMMMMMM               MMMMMMMMRRRRRRRR     RRRRRRR ...... 	DDDDDDDDDDDDD      EEEEEEEEEEEEEEEEEEEEEE            VVV            

@Controller
@RequestMapping("/titulos")
public class TituloController {
	
	private static final String CADASTRO_VIEW = "CadastroTitulo";

	@Autowired
	private Titulos titulos;

	@Autowired
	private CadastroTituloService cadastroTituloService;	
	
	@RequestMapping("/novo")
	public ModelAndView novo() {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject(new Titulo());
		//mv.addObject("todosStatusTitulo", StatusTitulo.values());
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView salvar(@Validated Titulo titulo, Errors errors, RedirectAttributes redirectAttributes) {
		
		ModelAndView mv = new ModelAndView("CadastroTitulo");
		if(errors.hasErrors()) {
			return mv;
		}
		
		try {		
			cadastroTituloService.salvar(titulo);
			
			mv.addObject("mensagem", "Título salvo com sucesso!");
			//mv.addObject("todosStatusTitulo", StatusTitulo.values());
			
			ModelAndView mv2 = new ModelAndView("redirect:/titulos/novo");
			redirectAttributes.addFlashAttribute("mensagem", "Título salvo com sucesso!");
			
			return mv2;
		}catch(IllegalArgumentException e) {
			errors.rejectValue("dataVencimento", null, e.getMessage());
			ModelAndView mv3 = new ModelAndView(CADASTRO_VIEW);
			return mv3;
		}
	}
	
	//@RequestParam(defaultValue = "%", required = false) String descricao
	
	@RequestMapping
	public ModelAndView pesquisar(@ModelAttribute("filtro") TituloFilter filtro) {
		List<Titulo> titulosFiltrados = cadastroTituloService.filtrar(filtro);
		
		ModelAndView mv = new ModelAndView("PesquisaTitulos");
		mv.addObject("titulos", titulosFiltrados);
		
		return mv;
	}
	
	/**
	 * Um recurco do Spring (JPA Repository) permite alterar desse codigo...
	@RequestMapping("{codigo}")
	public ModelAndView edicao(@PathVariable Long codigo) {
		Titulo titulo = titulos.findById(codigo).get();
		
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject(titulo);
		
		return mv;

	}
	*/
	
	/**
	 * ...para esse codigo
	 */
	@RequestMapping("{codigo}")
	public ModelAndView edicao(@PathVariable("codigo") Titulo titulo) {
		
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject(titulo);
		
		return mv;

	}	
	
	@RequestMapping(value="{codigo}", method = RequestMethod.DELETE)
	public String excluir(@PathVariable Long codigo, RedirectAttributes redirectAttributes) {
		cadastroTituloService.excluir(codigo);
		redirectAttributes.addFlashAttribute("mensagem", "Título excluído com sucesso!");
		return "redirect:/titulos";
	}
	
	@RequestMapping(value="/{codigo}/receber", method = RequestMethod.PUT)
	public @ResponseBody String receber(@PathVariable Long codigo, RedirectAttributes redirectAttributes) {
		System.out.println( " >>>> Codigo " + codigo );
		return cadastroTituloService.receber(codigo);
	}
	
	@ModelAttribute("todosStatus")
	public List<StatusTitulo> todosStatusTitulo(){
		return Arrays.asList( StatusTitulo.values() );
	}
	
	
}
